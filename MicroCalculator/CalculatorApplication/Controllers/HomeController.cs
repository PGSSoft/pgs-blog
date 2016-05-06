using System.Security.Policy;
using CalculationRequestParser;
using Microsoft.AspNet.Mvc;

namespace CalculatorApplication.Controllers
{
    public class HomeController : Controller
    {
        public IActionResult Index()
        {
            return View();
        }
    }

    public class OperationAvailable
    {
        public string Operation { get; set; }
        public bool Available { get; set; }

        public OperationAvailable(CalculationOperation operation, bool available)
        {
            Operation = operation.ToString();
            Available = available;
        }
    }
}
