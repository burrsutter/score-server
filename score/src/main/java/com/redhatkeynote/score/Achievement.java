/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhatkeynote.score;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "achievements")
public class Achievement implements Serializable, Cloneable {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="type")
    private String type;

    @Column(name="desc")
    private String desc;

    @Transient
    private boolean newAchievement;

    public Achievement() {
    }

    public Achievement(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Achievement(Integer id, String type, String desc, boolean newAchievement) {
        this.id = id;
        this.type = type;
        this.desc = desc;
        this.newAchievement = newAchievement;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isNewAchievement() {
        return newAchievement;
    }

    public void setNewAchievement(boolean newAchievement) {
        this.newAchievement = newAchievement;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Achievement) ) return false;

        Achievement that = (Achievement) o;

        if ( type != null ? !type.equals( that.type ) : that.type != null ) return false;
        return !(desc != null ? !desc.equals( that.desc ) : that.desc != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return desc;
    }

    @Override
    public Achievement clone()
            throws CloneNotSupportedException {
        return new Achievement( id, type, desc, newAchievement );
    }
}
