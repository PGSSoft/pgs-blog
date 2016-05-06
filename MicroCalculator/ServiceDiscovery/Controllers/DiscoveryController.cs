using System.Threading.Tasks;
using HttpCalculatorClient;
using ServicesContainer;

namespace ServiceDiscovery.Controllers
{
    using System.Linq;
    using Microsoft.AspNet.Mvc;
    using Microsoft.Extensions.Logging;

    [Route("api/service")]
    public class DiscoveryController : Controller
    {
        private readonly ILogger logger;
        private readonly ICalculatorServiceContainer serviceContainer;
        private readonly ICalculatorHttpClient httpClient;

        public DiscoveryController(
            ILogger<DiscoveryController> logger,
            ICalculatorServiceContainer serviceContainer,
            ICalculatorHttpClient httpClient)
        {
            this.logger = logger;
            this.serviceContainer = serviceContainer;
            this.httpClient = httpClient;
        }


        [HttpGet]
        public IActionResult Get(string serviceName, string version)
        {
            logger.LogInformation($"Get: servicename:{serviceName}");

            var registered = serviceContainer.Get(serviceName, version);
            if (registered.Any())
            {
                var result = string.Join(";", registered);
                return Ok(result);
            }

            return Ok(string.Empty);
        }

        [HttpPost]
        public async Task<IActionResult> Register(string servicename, string url, string version)
        {
            logger.LogInformation($"DiscoveryController::Register: sericename: {servicename} url: {url}");

            var response = await httpClient.GetHealthStatusOnApiUrl(url);
            if (response.IsSuccessStatusCode)
            {
                serviceContainer.Register(servicename, url, version);
                return Ok();
            }
            else
            {
                return HttpBadRequest("Registration failed, due to not responding to health check");
            }
        }
    }
}