using System.Threading.Tasks;
using CalculationRequestParser;
using Microsoft.AspNet.Mvc;

namespace ServiceOperation.Controllers
{
    [Route("api/operation")]
    public class OperationController : Controller
    {
        private IOperationParser parser;

        public OperationController(IOperationParser parser)
        {
            this.parser = parser;
        }

        public async Task<IActionResult> Get(string operation)
        {
            if (string.IsNullOrWhiteSpace(operation))
            {
                return HttpBadRequest("No operation to analyze");
            }
            var operations = await this.parser.ParseForCalculationOperations(operation);
            return Ok(operations);
        }
    }
}
