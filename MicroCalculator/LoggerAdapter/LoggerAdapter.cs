using System;

namespace LoggerAdapter
{
    public class LoggerAdapter : ILoggerAdapter
    {
        public void Log(string text)
        {
            Console.WriteLine(text);
        }
    }
}
