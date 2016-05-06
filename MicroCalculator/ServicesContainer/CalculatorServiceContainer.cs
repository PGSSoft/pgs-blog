using System.Collections.Generic;
using ContainerKey = System.Collections.Generic.KeyValuePair<string, string>;

namespace ServicesContainer
{
    public class CalculatorServiceContainer : ICalculatorServiceContainer
    {
        private static Dictionary<KeyValuePair<string, string>, List<string>> services = new Dictionary<ContainerKey, List<string>>();
        private static CalculatorServiceContainer instance = new CalculatorServiceContainer();
        public static CalculatorServiceContainer Instance => instance;

        public void Register(string serviceName, string url, string version)
        {
            var key = new ContainerKey(serviceName, version);
            AssureValueIsNotNullForKey(key);
            services[key].Add(url);
        }

        public Dictionary<KeyValuePair<string, string>, List<string>> GetAllServices()
        {
            var copy = new Dictionary<KeyValuePair<string, string>, List<string>>(services);
            return copy;
        }

        public List<string> Get(string serviceName, string version)
        {
            var key = new ContainerKey(serviceName, version);
            AssureValueIsNotNullForKey(key);
            return services[key];
        }

        private void AssureValueIsNotNullForKey(ContainerKey key)
        {
            if (services.ContainsKey(key) == false || services[key] == null)
            {
                services[key] = new List<string>();
            }
        }

        public void UseAsNewAvailableServices(Dictionary<ContainerKey, List<string>> healthyServices)
        {
            services = healthyServices;
        }
    }
}
