using System;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using CalculationRequestParser;
using HttpCalculatorClient;
using Microsoft.AspNet.Mvc;
using ServiceFinder;
using Microsoft.Extensions.Logging;

namespace ServiceMultiplication_v2.Controllers
{
    [Route("api/multiplication")]
    public class MultiplicationController : Controller
    {
        private readonly ICalculatorServiceFinder calculatorServiceFinder;
        private readonly ICalculatorHttpClient httpClient;
        private readonly ILogger logger;

        public MultiplicationController(
            ICalculatorServiceFinder calculatorServiceFinder,
            ICalculatorHttpClient httpClient,
            ILogger<MultiplicationController> logger)
        {
            this.calculatorServiceFinder = calculatorServiceFinder;
            this.httpClient = httpClient;
            this.logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> Get(decimal v1, decimal v2)
        {
            logger.LogInformation($"Get v1:{v1} v2:{v2}");
            await Task.Delay(200);
            if (v1 == 0)
            {
                return Ok(0);
            }

            if (v2 == 0)
            {
                return Ok(0);
            }


            //var serviceAddress = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Addition, "2.0");
            //var accumulator = v2;

            //for (int i = 1; i < v1; i++)
            //{
            //    var stringAsync = await httpClient.GetStringAsync(serviceAddress, CalculationOperation.Addition, accumulator, v2);
            //    accumulator = decimal.Parse(stringAsync);
            //}

            return Ok(v1*v2);
        }
    }
}
