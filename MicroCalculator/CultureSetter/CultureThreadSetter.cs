using System.Globalization;

namespace CultureSetter
{
    public static class CultureThreadSetter
    {
        public static void SetCultureThread(string cultureToSet = "en-US")
        {
            System.Threading.Thread.CurrentThread.CurrentCulture = new CultureInfo(cultureToSet);
            System.Threading.Thread.CurrentThread.CurrentUICulture = new CultureInfo(cultureToSet);
        }
    }
}
