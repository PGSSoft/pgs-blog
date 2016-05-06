using System.Threading.Tasks;
using Microsoft.AspNet.Mvc;
using Microsoft.Extensions.Logging;

namespace ServiceAddition_v2.Controllers
{
    [Route("api/addition")]
    public class AdditionController : Controller
    {
        private readonly ILogger logger;

        public AdditionController(ILogger<AdditionController> logger)
        {
            this.logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> Get(decimal v1, decimal v2)
        {
            logger.LogInformation($"Get: v1: {v1} v2: {v2}");
            await Task.Delay(100);
            return Ok(v1 + v2);
        }
    }
}
