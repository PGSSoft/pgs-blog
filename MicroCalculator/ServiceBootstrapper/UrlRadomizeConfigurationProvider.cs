using System;
using System.Collections.Generic;
using Microsoft.Extensions.Configuration;
using System.Linq;

namespace ServiceBootstrapper
{
    internal class UrlRadomizeConfigurationProvider : IConfigurationProvider
    {
        private IConfiguration _baseConfiguration;
        private Guid _randomValue = Guid.NewGuid();

        public UrlRadomizeConfigurationProvider(IConfiguration baseConfiguration)
        {
            _baseConfiguration = baseConfiguration;
        }

        public IEnumerable<string> GetChildKeys(IEnumerable<string> earlierKeys, string parentPath, string delimiter)
        {
            return _baseConfiguration.GetChildren().Select(child => child.Key);
        }

        public void Load()
        {
        }

        public void Set(string key, string value)
        {            
        }

        public bool TryGet(string key, out string value)
        {
            value = _baseConfiguration[key];

            if (string.IsNullOrEmpty(value))
            {
                return false;
            }

            if (!value.EndsWith("/"))
            {
                value += "/";
            }

            value += $"{_randomValue:N}/";

            return true;
        }
    }
}
