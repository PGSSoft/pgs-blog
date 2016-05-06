using System.Threading.Tasks;
using Microsoft.AspNet.Mvc;

namespace ServiceSubstraction_v2.Controllers
{
    [Route("api/substraction")]
    public class SubstractionController : Controller
    {
        public async Task<IActionResult> Get(decimal v1, decimal v2)
        {
            await Task.Delay(150);
            return Ok(v1 - v2);
        }
    }
}