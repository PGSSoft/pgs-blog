using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Threading.Tasks;
using CalculationRequestParser;
using HttpCalculatorClient;

namespace ServiceFinder
{
    public class CalculatorServiceFinder : ICalculatorServiceFinder
    {
        private readonly ICalculatorHttpClient httpClient;

        public CalculatorServiceFinder(ICalculatorHttpClient httpClient)
        {
            this.httpClient = httpClient;
        }

        public async Task<List<string>> FindByCalculatorOperation(CalculationOperation operation, string version)
        {
            var s = await httpClient.GetServicesString(operation, version);
            var services = s.Split(new[] { ";" }, StringSplitOptions.RemoveEmptyEntries).ToList();
            return services;
        }

        public async Task<List<string>> FindCalculationService(string version)
        {
            var s = await httpClient.GetOperationServices(version);
            var services = s.Split(new[] { ";" }, StringSplitOptions.RemoveEmptyEntries).ToList();
            return services;
        }
    }
}