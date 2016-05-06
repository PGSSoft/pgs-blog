namespace ServiceBase.Controllers
{
    using Microsoft.AspNet.Mvc;
    using Microsoft.Extensions.Logging;

    [Route("api/health")]
    public class HealthController : Controller
    {
        private readonly ILogger logger;

        public HealthController(ILogger<HealthController> logger)
        {
            this.logger = logger;
        }

        [HttpGet]
        public IActionResult Check()
        {
            logger.LogInformation($"Get");
            return Ok();
        }
    }
}
