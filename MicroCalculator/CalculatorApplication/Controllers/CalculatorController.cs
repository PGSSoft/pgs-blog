using System.Threading.Tasks;
using Microsoft.AspNet.Mvc;

namespace CalculatorApplication.Controllers
{
    public class CalculatorController : Controller
    {
        private readonly ICalculatorService calculatorService;

        public CalculatorController(ICalculatorService calculatorService)
        {
            this.calculatorService = calculatorService;
        }

        [HttpPost]
        public async Task<IActionResult> Index(string input, string version)
        {
            var calculateresult = await calculatorService.Calculate(input, version);
            return Ok(calculateresult);
        }
    }
}