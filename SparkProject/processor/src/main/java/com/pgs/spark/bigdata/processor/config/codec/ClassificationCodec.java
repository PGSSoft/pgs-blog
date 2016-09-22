package com.pgs.spark.bigdata.processor.config.codec;

import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import com.pgs.spark.bigdata.processor.domain.Classification;

/**
 * The type Classification codec.
 */
public final class ClassificationCodec extends EnumNameCodec<Classification> {

    /**
     * The constant instance.
     */
    public static final ClassificationCodec instance = new ClassificationCodec();

    private ClassificationCodec() {
        super(Classification.class);
    }

}
