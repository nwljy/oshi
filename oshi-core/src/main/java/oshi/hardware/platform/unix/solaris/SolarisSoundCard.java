/**
 * MIT License
 *
 * Copyright (c) 2010-2019 The OSHI project team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package oshi.hardware.platform.unix.solaris;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

/**
 * Solaris Sound Card.
 */
public class SolarisSoundCard extends AbstractSoundCard {

    private static final String LSHAL = "lshal";
    private static final String DEFAULT_AUDIO_DRIVER = "audio810";

    /**
     * <p>
     * Constructor for SolarisSoundCard.
     * </p>
     *
     * @param kernelVersion
     *            a {@link java.lang.String} object.
     * @param name
     *            a {@link java.lang.String} object.
     * @param codec
     *            a {@link java.lang.String} object.
     */
    public SolarisSoundCard(String kernelVersion, String name, String codec) {
        super(kernelVersion, name, codec);
    }

    /**
     * <p>
     * getSoundCards.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    public static List<SolarisSoundCard> getSoundCards() {
        Map<String, String> vendorMap = new HashMap<>();
        Map<String, String> productMap = new HashMap<>();
        List<String> sounds = new ArrayList<>();
        String key = "";
        for (String line : ExecutingCommand.runNative(LSHAL)) {
            line = line.trim();
            if (line.startsWith("udi =")) {
                // we have the key.
                key = ParseUtil.getSingleQuoteStringValue(line);
            } else if (!key.isEmpty() && !line.isEmpty()) {
                if (line.contains("info.solaris.driver =")
                        && DEFAULT_AUDIO_DRIVER.equals(ParseUtil.getSingleQuoteStringValue(line))) {
                    sounds.add(key);
                } else if (line.contains("info.product")) {
                    productMap.put(key, ParseUtil.getStringBetween(line, '\''));
                } else if (line.contains("info.vendor")) {
                    vendorMap.put(key, ParseUtil.getStringBetween(line, '\''));
                }
            }
        }
        List<SolarisSoundCard> soundCards = new ArrayList<>();
        for (String _key : sounds) {
            soundCards.add(new SolarisSoundCard(productMap.get(_key) + " " + DEFAULT_AUDIO_DRIVER,
                    vendorMap.get(_key) + " " + productMap.get(_key), productMap.get(_key)));
        }
        return soundCards;
    }
}
