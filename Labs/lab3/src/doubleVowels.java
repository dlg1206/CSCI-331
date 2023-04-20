import java.util.ArrayList;
import java.util.List;

/**
 * Check for double vowels
 * Not Found:   en
 * Found:       nl
 */
public class doubleVowels extends Feature {
    private final List<String> doubleVowels = new ArrayList<>() {
        {
            add("aa");
        }
    };

    @Override
    protected boolean isEnglish(Data data) {
        for (String pair : this.doubleVowels) {
            for (String word : data.getWords()) {
                if (word.contains(pair))
                    return false;
            }
        }
        return true;
    }

    @Override
    protected String getTestName() {
        return "Dutch Double Vowels";
    }
}
