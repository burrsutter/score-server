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
public class Achievement implements Serializable {

    public static Achievement[] ACHIEVEMENTS = new Achievement[]{
            new Achievement( "SCR_APP", "Apprentice Scorer" ),
            new Achievement( "SCR_EXP", "Expert Scorer" ),
            new Achievement( "SCR_MAS", "Master Scorer" ),
            new Achievement( "POP_APP", "Apprentice Popper" ),
            new Achievement( "POP_EXP", "Expert Popper" ),
            new Achievement( "POP_MAS", "Master Popper" ),
            new Achievement( "GLD_SNT", "Golden Snitch" )
    };

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="type")
    private String type;

    @Column(name="desc")
    private String desc;

    public Achievement() {
    }

    public Achievement(String type, String desc) {
        this.type = type;
        this.desc = desc;
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
}
