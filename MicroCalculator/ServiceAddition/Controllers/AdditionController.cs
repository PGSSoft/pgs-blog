namespace ServiceAddition.Controllers
{
    using Microsoft.AspNet.Mvc;
    using System.Threading.Tasks;
    using Microsoft.Extensions.Logging;

    [Route("api/addition")]
    public class AdditionController : Controller
    {
        private readonly ILogger logger;

        public AdditionController(ILogger<AdditionController> logger)
        {
            this.logger = logger;
        }

        [HttpGet]
        public async Task<IActionResult> Get(int v1, int v2)
        {
            logger.LogInformation($"Get: v1: {v1} v2: {v2}");
            await Task.Delay(100);
            return Ok(v1 + v2);
        }
    }
}
