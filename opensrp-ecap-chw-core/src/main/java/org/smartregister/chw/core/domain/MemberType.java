package org.smartregister.chw.core.domain;

import org.smartregister.chw.anc.domain.MemberObject;

public class MemberType {
    private MemberObject memberObject;
    private String memberType;

    public MemberType(MemberObject memberObject, String memberType) {
        this.memberObject = memberObject;
        this.memberType = memberType;
    }

    public MemberObject getMemberObject() {
        return memberObject;
    }

    public String getMemberType() {
        return memberType;
    }
}