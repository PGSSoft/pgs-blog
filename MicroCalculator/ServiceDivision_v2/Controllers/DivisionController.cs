using System;
using System.Threading;
using System.Threading.Tasks;
using CalculationRequestParser;
using HttpCalculatorClient;
using Microsoft.AspNet.Mvc;
using ServiceFinder;

namespace ServiceDivision_v2.Controllers
{
    [Route("api/division")]
    public class DivisionController : Controller
    {
        private readonly ICalculatorServiceFinder calculatorServiceFinder;
        private readonly ICalculatorHttpClient httpClient;

        public DivisionController(
            ICalculatorServiceFinder calculatorServiceFinder,
            ICalculatorHttpClient httpClient)
        {
            this.calculatorServiceFinder = calculatorServiceFinder;
            this.httpClient = httpClient;
        }

        [HttpGet]
        public async Task<IActionResult> Get(decimal v1, decimal v2)
        {
            await Task.Delay(300);
            if (v1 == 0)
            {
                return Ok(0);
            }

            if (v2 == 0)
            {
                return HttpBadRequest("Dont know how to divide by zero");
            }

            //var serviceAddress =
            //    await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Substraction, "2.0");

            //var accumulator = v1;
            //var result = 0m;
            //do
            //{
            //    var stringAsync =
            //        await
            //            httpClient.GetStringAsync(serviceAddress, CalculationOperation.Substraction, accumulator,
            //                Math.Abs(v2));
            //    accumulator = decimal.Parse(stringAsync);
            //    result++;
            //} while (accumulator > Math.Abs(v2));

            //    if (Math.Abs(v2) != v2)
            //{
            //    var multiplication = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Multiplication, "2.0");
            //    var r = await httpClient.GetStringAsync(multiplication, CalculationOperation.Multiplication, -1, result);
            //    result = decimal.Parse(r);
            //}

            return Ok(v1/v2);
        }
    }
}
