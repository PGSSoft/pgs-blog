namespace ServiceSubstraction.Controllers
{
    using System.Threading.Tasks;
    using Microsoft.AspNet.Mvc;

    [Route("api/substraction")]
    public class SubstractionController : Controller
    {
        public async Task<IActionResult> Get(int v1, int v2)
        {
            await Task.Delay(150);
            return Ok(v1 - v2);
        }
    }
}