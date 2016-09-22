package com.pgs.spark.bigdata.processor.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.UUID;

/**
 * The type Tag.
 */
@Table(name = "tags")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id", callSuper = false)
public class Tag extends CassandraEntity implements Serializable {

    private static final long serialVersionUID = 7152805838597912530L;

    @PartitionKey
    private UUID id;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "is_super_tag")
    private boolean isSuperTag;

    @Column(name = "is_assigned_to_supertag")
    private boolean isAssignedToSuperTag;

    @Column(name = "document_id")
    private UUID documentId;

    @Column(name = "result_id")
    private UUID resultId;

    @Column(name = "content")
    private String content;

    /**
     * Get isSuperTag
     * Due to cassandra naming convention lombok getters/setters will not work for boolean
     * @return isSuperTag
     */
    public boolean getIsSuperTag(){
        return isSuperTag;
    }

    public boolean getIsAssignedToSuperTag(){return isAssignedToSuperTag;}

    /**
     * Sets if tag is superTag
     * Due to cassandra naming convention lombok getters/setters will not work for boolean
     */
    public void setIsSuperTag(boolean isSuperTag){
        this.isSuperTag = isSuperTag;
    }

    public void setIsAssignedToSuperTag(boolean isAssignedToSuperTag){this.isAssignedToSuperTag = isAssignedToSuperTag;}

}
