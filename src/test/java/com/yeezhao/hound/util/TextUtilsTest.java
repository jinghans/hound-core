package com.yeezhao.hound.util;

import org.junit.Test;

import java.util.List;

/**
 * Created by SanDomingo on 5/26/14.
 */
public class TextUtilsTest {
    @Test
    public void testSplitText() throws Exception {
        String longText = "Billionaire candy tycoon Petro Poroshenko appeared to have won a decisive victory in Sunday's Ukrainian presidential election as early returns were announced Monday morning. \n" +
                "\n" +
                "With about 30 percent of precincts reporting early Monday, Poroshenko led a field of 21 candidates with 54 percent of the vote. Former Prime Minister Yulia Tymoshenko was running a distant second with 13 percent. Both results were in line with exit polls, which showed Poroshenko with nearly 56 percent and Tymoshenko with 13 percent.\n" +
                "\n" +
                "If Poroshenko's share of the vote remained above 50 percent, he would avoid a runoff against the second-place finisher, likely Tymoshenko, June 15. \n" +
                "\n" +
                "The vote took place amid weeks of fighting in eastern Ukraine where pro-Moscow separatists have seized government buildings and battled government troops in Europe's worst crisis since the end of the Cold War and the Balkan wars of the late 1990s. The rebels had vowed to block the ballot in the east, and less than 20 percent of the polling stations were open there after gunmen intimidated local residents by smashing ballot boxes, shutting down polling centers and issuing threats.\n" +
                "\n" +
                "But nationwide, about 60 percent of 35.5 million eligible voters turned out, the central elections commission said, and long lines snaked around polling stations in the capital, Kiev.\n" +
                "\n" +
                "Viewing the exit polls as definitive evidence of victory, Poroshenko said his first steps as president would be to visit the Donbass eastern industrial region, home to Ukraine's coal mines. He also promised a dialogue with residents of eastern Ukraine and said he was ready to extend amnesty to those who did not commit any crimes, saying he wanted to \"put an end to war, chaos, crime, and bring peace to the Ukrainian land.\"\n" +
                "\n" +
                "Poroshenko has said that he supports strong ties with the European Union, but also wants to mend the country's relations with Moscow, which were damaged by February's overthrow of pro-Moscow President Viktor Yanukovych. Russia responded by annexing Crimea in March. \n" +
                "\n" +
                "The Associated Press contributed to this report. ";
        List<String> result = TextUtils.splitText(longText, 1000);
        for (String chunk : result) {
            System.out.println(chunk.length() + " ==> " + chunk);
        }
    }

    @Test
    public void testTailorText() throws Exception {
        String longText = "Billionaire candy tycoon Petro Poroshenko appeared to have won a decisive victory in Sunday's Ukrainian presidential election as early returns were announced Monday morning. \n" +
                "\n" +
                "With about 30 percent of precincts reporting early Monday, Poroshenko led a field of 21 candidates with 54 percent of the vote. Former Prime Minister Yulia Tymoshenko was running a distant second with 13 percent. Both results were in line with exit polls, which showed Poroshenko with nearly 56 percent and Tymoshenko with 13 percent.\n" +
                "\n" +
                "If Poroshenko's share of the vote remained above 50 percent, he would avoid a runoff against the second-place finisher, likely Tymoshenko, June 15. \n" +
                "\n" +
                "The vote took place amid weeks of fighting in eastern Ukraine where pro-Moscow separatists have seized government buildings and battled government troops in Europe's worst crisis since the end of the Cold War and the Balkan wars of the late 1990s. The rebels had vowed to block the ballot in the east, and less than 20 percent of the polling stations were open there after gunmen intimidated local residents by smashing ballot boxes, shutting down polling centers and issuing threats.\n" +
                "\n" +
                "But nationwide, about 60 percent of 35.5 million eligible voters turned out, the central elections commission said, and long lines snaked around polling stations in the capital, Kiev.\n" +
                "\n" +
                "Viewing the exit polls as definitive evidence of victory, Poroshenko said his first steps as president would be to visit the Donbass eastern industrial region, home to Ukraine's coal mines. He also promised a dialogue with residents of eastern Ukraine and said he was ready to extend amnesty to those who did not commit any crimes, saying he wanted to \"put an end to war, chaos, crime, and bring peace to the Ukrainian land.\"\n" +
                "\n" +
                "Poroshenko has said that he supports strong ties with the European Union, but also wants to mend the country's relations with Moscow, which were damaged by February's overthrow of pro-Moscow President Viktor Yanukovych. Russia responded by annexing Crimea in March. \n" +
                "\n" +
                "The Associated Press contributed to this report. ";
        String chunk = TextUtils.tailorText(longText, 1300);
        System.out.println(chunk.length() + " ==> " + chunk);
    }



}
