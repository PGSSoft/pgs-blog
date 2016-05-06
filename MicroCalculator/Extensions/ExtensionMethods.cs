using System;
using System.Collections.Generic;
using System.Linq;

namespace Extensions
{
    public static class ExtensionMethods
    {
        public static string Random(this List<string> list)
        {
            if (list.Count == 1)
            {
                return list.First();
            }

            var random = new Random(DateTime.Now.Millisecond);
            var next = random.Next(list.Count);
            return list.ElementAt(next);
        }
    }
}
