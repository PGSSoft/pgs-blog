using System;
using CalculationRequestParser;
using HttpCalculatorClient;
using ServiceFinder;

namespace ServiceDivision.Controllers
{
    using System.Threading;
    using System.Threading.Tasks;
    using Microsoft.AspNet.Mvc;

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
        public async Task<IActionResult> Get(int v1, int v2)
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

            //var serviceAddress = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Substraction, "1.0");

            //var accumulator = v1;
            //var result = 0;
            //do
            //{
            //    var stringAsync =
            //        await
            //            httpClient.GetStringAsync(serviceAddress, CalculationOperation.Substraction, accumulator,
            //                Math.Abs(v2));
            //    accumulator = int.Parse(stringAsync);
            //    result++;
            //} while (accumulator > Math.Abs(v2));

            //if (Math.Abs(v2) != v2)
            //{
            //    var multiplication = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Multiplication, "1.0");
            //    var r = await httpClient.GetStringAsync(multiplication, CalculationOperation.Multiplication, -1, result);
            //    result = int.Parse(r);
            //}

            return Ok(v1 / v2);
        }
    }
}