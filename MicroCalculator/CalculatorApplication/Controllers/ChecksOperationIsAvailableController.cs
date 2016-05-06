using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using CalculationRequestParser;
using HttpCalculatorClient;
using Microsoft.AspNet.Mvc;
using ServiceFinder;

namespace CalculatorApplication.Controllers
{
    public class ChecksOperationIsAvailableController : Controller
    {
        private readonly ICalculatorServiceFinder calculatorServiceFinder;

        public ChecksOperationIsAvailableController(ICalculatorServiceFinder calculatorServiceFinder)
        {
            this.calculatorServiceFinder = calculatorServiceFinder;
        }

        public async Task<IActionResult> Index(string version)
        {
            var add = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Addition, version);
            var sub = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Substraction, version);
            var mul = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Multiplication, version);
            var div = await calculatorServiceFinder.FindByCalculatorOperation(CalculationOperation.Division, version);


            var addAvail = new OperationAvailable(CalculationOperation.Addition, add.Any());
            var subAvail = new OperationAvailable(CalculationOperation.Substraction, sub.Any());
            var mulAvail = new OperationAvailable(CalculationOperation.Multiplication, mul.Any());
            var divAvail = new OperationAvailable(CalculationOperation.Division, div.Any());

            var operationAvailables = new List<OperationAvailable> {addAvail, subAvail, mulAvail, divAvail};
            return Ok(operationAvailables);
        }
    }
}