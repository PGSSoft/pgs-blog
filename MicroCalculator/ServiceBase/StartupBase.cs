namespace ServiceBase
{
    using Log4netLogger;
    using Microsoft.AspNet.Builder;
    using Microsoft.Extensions.DependencyInjection;
    using Microsoft.Extensions.Logging;
    using Microsoft.Extensions.PlatformAbstractions;
    using System.ComponentModel.Design;

    public abstract class StartupBase
    {
        public virtual void ConfigureServices(IServiceCollection services)
        {
            services.AddMvc();
            services.Add(new ServiceDescriptor(typeof(IServiceContainer), typeof(ServiceContainer), ServiceLifetime.Singleton));
        }

        public virtual void Configure(IApplicationBuilder app, IApplicationEnvironment env, ILoggerFactory logFactory)
        {
            env.ConfigureLog4net();

            app.UseIISPlatformHandler();
            app.UseMvc();

            logFactory
                .AddConsole(LogLevel.Information)
                .AddLog4net();

            CultureSetter.CultureThreadSetter.SetCultureThread();
        }
    }
}
