package com.pgs.spark.bigdata.config.cassandra.codec;

import com.datastax.driver.extras.codecs.enums.EnumNameCodec;
import com.pgs.spark.bigdata.domain.enumeration.Classification;

public final class ClassificationCodec extends EnumNameCodec<Classification> {

    public static final ClassificationCodec instance = new ClassificationCodec();

    private ClassificationCodec() {
        super(Classification.class);
    }

}
