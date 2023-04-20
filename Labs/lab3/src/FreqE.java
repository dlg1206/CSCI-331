/**
 * Test the frequency of e's
 * e's are approx 17% of nl and 13% of en
 * Split at 15%
 * > 15%:  nl
 * <= 15%:  en
 */
public class FreqE extends Feature {
    @Override
    protected boolean isEnglish(Data data) {
        return data.getPercent('e') <= 0.15;
    }

    @Override
    protected String getTestName() {
        return "E Frequency";
    }
}
