using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using CalculationRequestParser;
using Extensions;
using HttpCalculatorClient;
using Microsoft.Dnx.Compilation;
using Microsoft.Extensions.WebEncoders;
using Newtonsoft.Json;
using ServiceFinder;

namespace CalculatorApplication.Controllers
{
    public interface ICalculatorService
    {
        Task<string> Calculate(string input, string version);
    }

    public class CalculatorService : ICalculatorService
    {
        private readonly ICalculatorServiceFinder calculatorServiceFinder;
        private readonly ICalculatorHttpClient httpClient;

        public CalculatorService(ICalculatorServiceFinder calculatorServiceFinder,
            ICalculatorHttpClient httpClient)
        {
            this.calculatorServiceFinder = calculatorServiceFinder;
            this.httpClient = httpClient;
        }

        public async Task<string> Calculate(string input, string version)
        {
            var tasks = new List<CalculationTask>();
            var sb = new StringBuilder(input);
            List<CalculatorOperation> list = null;
            do
            {
                tasks.Clear();
                list = await GetCalculatorOperations(sb.ToString(), version);
                sb.Clear();
                for (var i = 0; i < list.Count / 2; i++)
                {
                    var item1 = list[i * 2];
                    var item2 = list[i * 2 + 1];
                    tasks.Add(this.CreateCalculationTask(item1, item2, version));
                }

                Task.WaitAll(tasks.Select(x => x.Task).ToArray());
                tasks.ForEach(t => sb.Append($"{t.Task.Result.Result}{t.NextOperation.AsMathSymbol()}"));
                if (list.Count % 2 == 1)
                {
                    sb.Append($"{list.Last().Value}");
                }
            } while (list.Count > 2);


            return await tasks.Single().Task.Result;
        }

        private async Task<List<CalculatorOperation>> GetCalculatorOperations(string input, string version)
        {
            var s = await this.calculatorServiceFinder.FindCalculationService("1.0");
            var operationsToPerform = await this.httpClient.GetCalculationOperations(s, input, version);
            var list = JsonConvert.DeserializeObject<List<CalculatorOperation>>(operationsToPerform);
            return list;
        }

        private CalculationTask CreateCalculationTask(CalculatorOperation item1, CalculatorOperation item2, string version)
        {
            var operationtask = calculatorServiceFinder.FindByCalculatorOperation(item2.Operation, version);
            var continueWith = operationtask.ContinueWith(operation =>
                httpClient.GetStringAsync(operation.Result, item2.Operation, item1.Value, item2.Value));


            return new CalculationTask(continueWith, item2.Operation);
        }
    }

    internal class CalculationTask
    {
        public CalculationTask(Task<Task<string>> task, CalculationOperation nextOperation)
        {
            Task = task;
            NextOperation = nextOperation;
        }

        public Task<Task<string>> Task { get; set; }
        public CalculationOperation NextOperation { get; set; }

    }
}