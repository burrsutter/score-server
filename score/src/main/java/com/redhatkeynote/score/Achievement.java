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
@NamedQueries({
    @NamedQuery(name="getAchievements", query="from Achievement a"),
    @NamedQuery(name="findAchievementByTypeTeamScores", query="from Achievement a where a.type = :type"),
})
public class Achievement implements Serializable, Cloneable {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;

    @Column(name="type")
    private String type;

    @Column(name="description")
    private String description;

    public Achievement() {
    }

    public Achievement(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public Achievement(Integer id, String type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof Achievement) ) return false;

        Achievement that = (Achievement) o;

        if ( type != null ? !type.equals( that.type ) : that.type != null ) return false;
        return !(description != null ? !description.equals( that.description ) : that.description != null);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public Achievement clone()
            throws CloneNotSupportedException {
        return new Achievement( id, type, description );
    }
}
