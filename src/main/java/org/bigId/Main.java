package org.bigId;

import org.bigId.core.Run;

public class Main {

    private static final String matchValue = """
            James,John,Robert,Michael,William,David,Richard,Charles,Joseph,Thomas,Christopher,Daniel,Paul,Mark,
            Donald,George,Kenneth,Steven,Edward,Brian,Ronald,Anthony,Kevin,Jason,Matthew,Gary,Timothy,Jose,
            Larry,Jeffrey,Frank,Scott,Eric,Stephen,Andrew,Raymond,Gregory,Joshua,Jerry,Dennis,Walter,Patrick,
            Peter,Harold,Douglas,Henry,Carl,Arthur,Ryan,Roger""";

    public static void main(String[] args) {
        new Run("big.txt", matchValue).scan();
    }
}