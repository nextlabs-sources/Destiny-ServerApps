/*
 * Copyright 2020 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Feb 28, 2020
 *
 */
package com.nextlabs.destiny.console.model.dictionary;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DICT_LEAF_ELEMENTS")
public class LeafElement {

    @Id
    @Column(name = "element_id")
    private Long elementId;

    @Column(name = "type_id")
    private Long typeId;

    @Column
    private String string00, string01, string02, string03, string04, string05, string06, string07,
            string08, string09, string10, string11, string12, string13, string14, string15,
            string16, string17, string18, string19, string20, string21, string22, string23,
            string24, string25, string26, string27, string28, string29, string30, string31,
            string32, string33, string34, string35, string36, string37, string38, string39,
            string40, string41, string42, string43, string44, string45, string46, string47,
            string48, string49;

    @Column
    private Long number00, number01, number02, number03, number04, number05, number06, number07,
            number08, number09, number10, number11, number12, number13, number14, number15,
            number16, number17, number18, number19;

    @Column
    private Long Date00, Date01, Date02, Date03, Date04, Date05, Date06, Date07, Date08, Date09,
            Date10, Date11, Date12, Date13, Date14, Date15, Date16, Date17, Date18, Date19;

    @Column
    private Long[] numArray00, numArray01, numArray02, numArray03;

    public Long getElementId() {
        return elementId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getString00() {
        return string00;
    }

    public void setString00(String string00) {
        this.string00 = string00;
    }

    public String getString01() {
        return string01;
    }

    public void setString01(String string01) {
        this.string01 = string01;
    }

    public String getString02() {
        return string02;
    }

    public void setString02(String string02) {
        this.string02 = string02;
    }

    public String getString03() {
        return string03;
    }

    public void setString03(String string03) {
        this.string03 = string03;
    }

    public String getString04() {
        return string04;
    }

    public void setString04(String string04) {
        this.string04 = string04;
    }

    public String getString05() {
        return string05;
    }

    public void setString05(String string05) {
        this.string05 = string05;
    }

    public String getString06() {
        return string06;
    }

    public void setString06(String string06) {
        this.string06 = string06;
    }

    public String getString07() {
        return string07;
    }

    public void setString07(String string07) {
        this.string07 = string07;
    }

    public String getString08() {
        return string08;
    }

    public void setString08(String string08) {
        this.string08 = string08;
    }

    public String getString09() {
        return string09;
    }

    public void setString09(String string09) {
        this.string09 = string09;
    }

    public String getString10() {
        return string10;
    }

    public void setString10(String string10) {
        this.string10 = string10;
    }

    public String getString11() {
        return string11;
    }

    public void setString11(String string11) {
        this.string11 = string11;
    }

    public String getString12() {
        return string12;
    }

    public void setString12(String string12) {
        this.string12 = string12;
    }

    public String getString13() {
        return string13;
    }

    public void setString13(String string13) {
        this.string13 = string13;
    }

    public String getString14() {
        return string14;
    }

    public void setString14(String string14) {
        this.string14 = string14;
    }

    public String getString15() {
        return string15;
    }

    public void setString15(String string15) {
        this.string15 = string15;
    }

    public String getString16() {
        return string16;
    }

    public void setString16(String string16) {
        this.string16 = string16;
    }

    public String getString17() {
        return string17;
    }

    public void setString17(String string17) {
        this.string17 = string17;
    }

    public String getString18() {
        return string18;
    }

    public void setString18(String string18) {
        this.string18 = string18;
    }

    public String getString19() {
        return string19;
    }

    public void setString19(String string19) {
        this.string19 = string19;
    }

    public String getString20() {
        return string20;
    }

    public String getString21() {
        return string21;
    }

    public String getString22() {
        return string22;
    }

    public String getString23() {
        return string23;
    }

    public String getString24() {
        return string24;
    }

    public String getString25() {
        return string25;
    }

    public String getString26() {
        return string26;
    }

    public String getString27() {
        return string27;
    }

    public String getString28() {
        return string28;
    }

    public String getString29() {
        return string29;
    }

    public String getString30() {
        return string30;
    }

    public String getString31() {
        return string31;
    }

    public String getString32() {
        return string32;
    }

    public String getString33() {
        return string33;
    }

    public String getString34() {
        return string34;
    }

    public String getString35() {
        return string35;
    }

    public String getString36() {
        return string36;
    }

    public String getString37() {
        return string37;
    }

    public String getString38() {
        return string38;
    }

    public String getString39() {
        return string39;
    }

    public String getString40() {
        return string40;
    }

    public String getString41() {
        return string41;
    }

    public String getString42() {
        return string42;
    }

    public String getString43() {
        return string43;
    }

    public String getString44() {
        return string44;
    }

    public String getString45() {
        return string45;
    }

    public String getString46() {
        return string46;
    }

    public String getString47() {
        return string47;
    }

    public String getString48() {
        return string48;
    }

    public String getString49() {
        return string49;
    }

    public void setString20(String string20) {
        this.string20 = string20;
    }

    public void setString21(String string21) {
        this.string21 = string21;
    }

    public void setString22(String string22) {
        this.string22 = string22;
    }

    public void setString23(String string23) {
        this.string23 = string23;
    }

    public void setString24(String string24) {
        this.string24 = string24;
    }

    public void setString25(String string25) {
        this.string25 = string25;
    }

    public void setString26(String string26) {
        this.string26 = string26;
    }

    public void setString27(String string27) {
        this.string27 = string27;
    }

    public void setString28(String string28) {
        this.string28 = string28;
    }

    public void setString29(String string29) {
        this.string29 = string29;
    }

    public void setString30(String string30) {
        this.string30 = string30;
    }

    public void setString31(String string31) {
        this.string31 = string31;
    }

    public void setString32(String string32) {
        this.string32 = string32;
    }

    public void setString33(String string33) {
        this.string33 = string33;
    }

    public void setString34(String string34) {
        this.string34 = string34;
    }

    public void setString35(String string35) {
        this.string35 = string35;
    }

    public void setString36(String string36) {
        this.string36 = string36;
    }

    public void setString37(String string37) {
        this.string37 = string37;
    }

    public void setString38(String string38) {
        this.string38 = string38;
    }

    public void setString39(String string39) {
        this.string39 = string39;
    }

    public void setString40(String string40) {
        this.string40 = string40;
    }

    public void setString41(String string41) {
        this.string41 = string41;
    }

    public void setString42(String string42) {
        this.string42 = string42;
    }

    public void setString43(String string43) {
        this.string43 = string43;
    }

    public void setString44(String string44) {
        this.string44 = string44;
    }

    public void setString45(String string45) {
        this.string45 = string45;
    }

    public void setString46(String string46) {
        this.string46 = string46;
    }

    public void setString47(String string47) {
        this.string47 = string47;
    }

    public void setString48(String string48) {
        this.string48 = string48;
    }

    public void setString49(String string49) {
        this.string49 = string49;
    }

    public Long getNumber00() {
        return number00;
    }

    public void setNumber00(Long number00) {
        this.number00 = number00;
    }

    public Long getNumber01() {
        return number01;
    }

    public void setNumber01(Long number01) {
        this.number01 = number01;
    }

    public Long getNumber02() {
        return number02;
    }

    public void setNumber02(Long number02) {
        this.number02 = number02;
    }

    public Long getNumber03() {
        return number03;
    }

    public void setNumber03(Long number03) {
        this.number03 = number03;
    }

    public Long getNumber04() {
        return number04;
    }

    public void setNumber04(Long number04) {
        this.number04 = number04;
    }

    public Long getNumber05() {
        return number05;
    }

    public void setNumber05(Long number05) {
        this.number05 = number05;
    }

    public Long getNumber06() {
        return number06;
    }

    public void setNumber06(Long number06) {
        this.number06 = number06;
    }

    public Long getNumber07() {
        return number07;
    }

    public void setNumber07(Long number07) {
        this.number07 = number07;
    }

    public Long getNumber08() {
        return number08;
    }

    public void setNumber08(Long number08) {
        this.number08 = number08;
    }

    public Long getNumber09() {
        return number09;
    }

    public void setNumber09(Long number09) {
        this.number09 = number09;
    }

    public Long getNumber10() {
        return number10;
    }

    public void setNumber10(Long number10) {
        this.number10 = number10;
    }

    public Long getNumber11() {
        return number11;
    }

    public void setNumber11(Long number11) {
        this.number11 = number11;
    }

    public Long getNumber12() {
        return number12;
    }

    public void setNumber12(Long number12) {
        this.number12 = number12;
    }

    public Long getNumber13() {
        return number13;
    }

    public void setNumber13(Long number13) {
        this.number13 = number13;
    }

    public Long getNumber14() {
        return number14;
    }

    public void setNumber14(Long number14) {
        this.number14 = number14;
    }

    public Long getNumber15() {
        return number15;
    }

    public void setNumber15(Long number15) {
        this.number15 = number15;
    }

    public Long getNumber16() {
        return number16;
    }

    public void setNumber16(Long number16) {
        this.number16 = number16;
    }

    public Long getNumber17() {
        return number17;
    }

    public void setNumber17(Long number17) {
        this.number17 = number17;
    }

    public Long getNumber18() {
        return number18;
    }

    public void setNumber18(Long number18) {
        this.number18 = number18;
    }

    public Long getNumber19() {
        return number19;
    }

    public void setNumber19(Long number19) {
        this.number19 = number19;
    }

    public Long getDate00() {
        return Date00;
    }

    public void setDate00(Long date00) {
        Date00 = date00;
    }

    public Long getDate01() {
        return Date01;
    }

    public void setDate01(Long date01) {
        Date01 = date01;
    }

    public Long getDate02() {
        return Date02;
    }

    public void setDate02(Long date02) {
        Date02 = date02;
    }

    public Long getDate03() {
        return Date03;
    }

    public void setDate03(Long date03) {
        Date03 = date03;
    }

    public Long getDate04() {
        return Date04;
    }

    public void setDate04(Long date04) {
        Date04 = date04;
    }

    public Long getDate05() {
        return Date05;
    }

    public void setDate05(Long date05) {
        Date05 = date05;
    }

    public Long getDate06() {
        return Date06;
    }

    public void setDate06(Long date06) {
        Date06 = date06;
    }

    public Long getDate07() {
        return Date07;
    }

    public void setDate07(Long date07) {
        Date07 = date07;
    }

    public Long getDate08() {
        return Date08;
    }

    public void setDate08(Long date08) {
        Date08 = date08;
    }

    public Long getDate09() {
        return Date09;
    }

    public void setDate09(Long date09) {
        Date09 = date09;
    }

    public Long getDate10() {
        return Date10;
    }

    public void setDate10(Long date10) {
        Date10 = date10;
    }

    public Long getDate11() {
        return Date11;
    }

    public void setDate11(Long date11) {
        Date11 = date11;
    }

    public Long getDate12() {
        return Date12;
    }

    public void setDate12(Long date12) {
        Date12 = date12;
    }

    public Long getDate13() {
        return Date13;
    }

    public void setDate13(Long date13) {
        Date13 = date13;
    }

    public Long getDate14() {
        return Date14;
    }

    public void setDate14(Long date14) {
        Date14 = date14;
    }

    public Long getDate15() {
        return Date15;
    }

    public void setDate15(Long date15) {
        Date15 = date15;
    }

    public Long getDate16() {
        return Date16;
    }

    public void setDate16(Long date16) {
        Date16 = date16;
    }

    public Long getDate17() {
        return Date17;
    }

    public void setDate17(Long date17) {
        Date17 = date17;
    }

    public Long getDate18() {
        return Date18;
    }

    public void setDate18(Long date18) {
        Date18 = date18;
    }

    public Long getDate19() {
        return Date19;
    }

    public void setDate19(Long date19) {
        Date19 = date19;
    }

    public Long[] getNumArray00() {
        return numArray00;
    }

    public void setNumArray00(Long[] numArray00) {
        this.numArray00 = numArray00;
    }

    public Long[] getNumArray01() {
        return numArray01;
    }

    public void setNumArray01(Long[] numArray01) {
        this.numArray01 = numArray01;
    }

    public Long[] getNumArray02() {
        return numArray02;
    }

    public void setNumArray02(Long[] numArray02) {
        this.numArray02 = numArray02;
    }

    public Long[] getNumArray03() {
        return numArray03;
    }

    public void setNumArray03(Long[] numArray03) {
        this.numArray03 = numArray03;
    }

}
