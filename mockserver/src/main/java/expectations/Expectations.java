package expectations;

import org.mockserver.mock.Expectation;

import java.util.ArrayList;
import java.util.List;

public final class Expectations {
    public static Expectation[] getExpectations() {
        List<Expectation> expectations = new ArrayList<>() {
            {
                add(new GetAvailableShops().createExp());
            }

            {
                add(new GetShopsGoods().createExp());
            }

            {
                add(new AddShop().createExp());
            }

            {
                add(new AddGoodsToTheShop().createExp());
            }

            {
                add(new RemoveGoodsFromTheShop().createExp());
            }
        };

        return expectations.toArray(Expectation[]::new);
    }
}
