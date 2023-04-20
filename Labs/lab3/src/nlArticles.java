import java.util.ArrayList;
import java.util.List;

/**
 * Check for Dutch articles
 * Not Found:   nl
 * Found:       en
 */
public class nlArticles extends Feature {
    private final List<String> articles = new ArrayList<>() {
        {
            add("de");
            add("het");
            add("een");
            add("der");
            add("des");
            add("den");
        }
    };

    @Override
    protected boolean isEnglish(Data data) {
        for (String word : this.articles)
            if (data.containsWord(word))
                return false;
        return true;
    }

    @Override
    protected String getTestName() {
        return "Dutch Articles";
    }
}
