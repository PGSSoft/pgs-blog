using CalculationRequestParser;

namespace Registrator
{
    using System.Threading.Tasks;
    using System.Collections.Generic;
    using System.Net.Http;
    using Microsoft.Extensions.Configuration;

    public class ServiceRegistrator
    {
        public ServiceRegistrator(IConfiguration configuration)
        {
            Url = configuration["server.urls"];
        }

        public string Url { get; set; }


        public async Task Register(CalculationOperation servicename, string version)
        {
            var httpClient = new HttpClient();
            var content = this.PrepareRegistrationContent(servicename.ToString().ToLower(), version);

            await httpClient.PostAsync("http://localhost:55000/api/service", content);
        }

        private FormUrlEncodedContent PrepareRegistrationContent(string servicename, string version)
        {
            var name = new KeyValuePair<string, string>("servicename", servicename);
            var url = new KeyValuePair<string, string>("url", this.Url);
            var ver = new KeyValuePair<string, string>("version", version);
            var keyValuePairs = new List<KeyValuePair<string, string>>();

            keyValuePairs.Add(name);
            keyValuePairs.Add(url);
            keyValuePairs.Add(ver);
            var content = new FormUrlEncodedContent(keyValuePairs);

            return content;
        }

        public async Task RegisterOperationService(string version)
        {
            var httpClient = new HttpClient();
            var content = this.PrepareRegistrationContent("operation", version);
            await httpClient.PostAsync("http://localhost:55000/api/service", content);
        }
    }
}