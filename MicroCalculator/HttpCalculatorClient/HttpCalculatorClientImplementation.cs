using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using CalculationRequestParser;
using Extensions;
using Microsoft.Extensions.WebEncoders;

namespace HttpCalculatorClient
{
    public class HttpCalculatorClientImplementation : ICalculatorHttpClient
    {
        public async Task<HttpResponseMessage> GetHealthStatusOnApiUrl(string url)
        {
            var client = new HttpClient();
            return await client.GetAsync($"{url}/api/health");
        }

        public Task<string> GetStringAsync<T>(List<string> serviceAddress, CalculationOperation operation, T v1, T v2)
        {
            var serviceClientAddress = $"{serviceAddress.Random()}/api/{operation.ToString().ToLower()}?v1={v1:G29}&v2={v2:G29}";
            var client = new HttpClient();
            return client.GetStringAsync(serviceClientAddress);
        }

        public async Task<string> GetServicesString(CalculationOperation operation, string version)
        {
            var httpClient = new HttpClient();
            return await httpClient.GetStringAsync($"http://localhost:55000/api/service?serviceName={operation.ToString().ToLower()}&version={version}");
        }

        public async Task<string> GetOperationServices(string version)
        {
            var httpClient = new HttpClient();
            return await httpClient.GetStringAsync($"http://localhost:55000/api/service?serviceName=operation&version={version}");
        }

        public async Task<string> GetCalculationOperations(List<string> availableServices, string input, string version)
        {
            var encodedInput = new UrlEncoder().UrlEncode(input);
            var url = $"{availableServices.Random()}/api/operation?operation={encodedInput}";
            var httpClient = new HttpClient();
            return await httpClient.GetStringAsync(url);
        }
    }
}