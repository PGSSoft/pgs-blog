using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Threading.Tasks;
using CalculatorApplication.Controllers;
using HttpCalculatorClient;
using Microsoft.AspNet.Builder;
using Microsoft.AspNet.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using ServiceFinder;
using Log4netLogger;

namespace CalculatorApplication
{
    public class Startup
    {
        public Startup(IHostingEnvironment env)
        {
            // Set up configuration sources.
            var builder = new ConfigurationBuilder()
                .AddJsonFile("appsettings.json")
                .AddEnvironmentVariables();
            Configuration = builder.Build();
        }

        public IConfigurationRoot Configuration { get; set; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            // Add framework services.
            services.Add(new ServiceDescriptor(typeof(ICalculatorServiceFinder), typeof(CalculatorServiceFinder), ServiceLifetime.Scoped));
            services.Add(new ServiceDescriptor(typeof(ICalculatorService), typeof(CalculatorService), ServiceLifetime.Scoped));
            services.Add(new ServiceDescriptor(typeof(ICalculatorHttpClient), typeof(HttpCalculatorClientImplementation), ServiceLifetime.Scoped));
            services.AddMvc();
        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IHostingEnvironment env, ILoggerFactory loggerFactory)
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
            System.Threading.Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
            CultureSetter.CultureThreadSetter.SetCultureThread();
            loggerFactory
                .AddConsole(Configuration.GetSection("Logging"))
                .AddDebug()
                .AddLog4net();

            if (env.IsDevelopment())
            {
                app.UseBrowserLink();
                app.UseDeveloperExceptionPage();
            }
            else
            {
                app.UseExceptionHandler("/Home/Error");
            }

            app.UseIISPlatformHandler();

            app.UseStaticFiles();

            app.UseMvc(routes =>
            {
                routes.MapRoute(
                    name: "default",
                    template: "{controller=Home}/{action=Index}/{id?}");
            });
        }

        // Entry point for the application.
        public static void Main(string[] args) => WebApplication.Run<Startup>(args);
    }
}
