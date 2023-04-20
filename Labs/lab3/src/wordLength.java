/**
 * Checks for the word length
 * >6 chars:    nl
 * <= 6 chars:  en
 */
public class wordLength extends Feature {

    @Override
    protected boolean isEnglish(Data data) {

        for (String word : data.getWords()) {
            if (word.length() > 6)
                return false;
        }
        return true;
    }

    @Override
    protected String getTestName() {
        return "check for double vowels";
    }
}
