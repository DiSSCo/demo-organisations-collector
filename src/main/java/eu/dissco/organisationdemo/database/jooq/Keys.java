/*
 * This file is generated by jOOQ.
 */
package eu.dissco.organisationdemo.database.jooq;


import eu.dissco.organisationdemo.database.jooq.tables.OrganisationDo;
import eu.dissco.organisationdemo.database.jooq.tables.records.OrganisationDoRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<OrganisationDoRecord> ORGANISATION_DO_PKEY = Internal.createUniqueKey(OrganisationDo.ORGANISATION_DO, DSL.name("organisation_do_pkey"), new TableField[] { OrganisationDo.ORGANISATION_DO.ID }, true);
}
