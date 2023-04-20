/**
 * Test the number of times 't' occurs
 * t is the second most common letter in English
 * letterFreq[2] != 't':   nl
 * letterFreq[2] == 't':   en
 */
public class tCount extends Feature {
    @Override
    protected boolean isEnglish(Data data) {
        return data.getFrequencyIndex('t') == 2;
    }

    @Override
    protected String getTestName() {
        return "'T' test";
    }
}
