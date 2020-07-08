package io.koschicken.listener;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

@Service
public class DiceListener {

    private int roll() {
        return RandomUtils.nextInt(1, 7);
    }
}
