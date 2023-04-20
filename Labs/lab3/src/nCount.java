/**
 * Test the number of times 'n' occurs
 * n is the second most common letter in Dutch
 * letterFreq[2] == 'n':   nl
 * letterFreq[2] != 'n':   en
 */
public class nCount extends Feature {
    @Override
    protected boolean isEnglish(Data data) {
        return data.getFrequencyIndex('n') != 2;
    }

    @Override
    protected String getTestName() {
        return "'N' test";
    }
}
