//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.10
//
// <auto-generated>
//
// Generated from file `VotingConsultation.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package VotingConsultation;

public interface Publisher extends com.zeroc.Ice.Object
{
    void addSubscriber(String name, SubscriberPrx subscriber, com.zeroc.Ice.Current current);

    void removeSubscriber(String name, com.zeroc.Ice.Current current);

    /** @hidden */
    static final String[] _iceIds =
    {
        "::Ice::Object",
        "::VotingConsultation::Publisher"
    };

    @Override
    default String[] ice_ids(com.zeroc.Ice.Current current)
    {
        return _iceIds;
    }

    @Override
    default String ice_id(com.zeroc.Ice.Current current)
    {
        return ice_staticId();
    }

    static String ice_staticId()
    {
        return "::VotingConsultation::Publisher";
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_addSubscriber(Publisher obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_name;
        SubscriberPrx iceP_subscriber;
        iceP_name = istr.readString();
        iceP_subscriber = SubscriberPrx.uncheckedCast(istr.readProxy());
        inS.endReadParams();
        obj.addSubscriber(iceP_name, iceP_subscriber, current);
        return inS.setResult(inS.writeEmptyParams());
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_removeSubscriber(Publisher obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_name;
        iceP_name = istr.readString();
        inS.endReadParams();
        obj.removeSubscriber(iceP_name, current);
        return inS.setResult(inS.writeEmptyParams());
    }

    /** @hidden */
    final static String[] _iceOps =
    {
        "addSubscriber",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping",
        "removeSubscriber"
    };

    /** @hidden */
    @Override
    default java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceDispatch(com.zeroc.IceInternal.Incoming in, com.zeroc.Ice.Current current)
        throws com.zeroc.Ice.UserException
    {
        int pos = java.util.Arrays.binarySearch(_iceOps, current.operation);
        if(pos < 0)
        {
            throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
        }

        switch(pos)
        {
            case 0:
            {
                return _iceD_addSubscriber(this, in, current);
            }
            case 1:
            {
                return com.zeroc.Ice.Object._iceD_ice_id(this, in, current);
            }
            case 2:
            {
                return com.zeroc.Ice.Object._iceD_ice_ids(this, in, current);
            }
            case 3:
            {
                return com.zeroc.Ice.Object._iceD_ice_isA(this, in, current);
            }
            case 4:
            {
                return com.zeroc.Ice.Object._iceD_ice_ping(this, in, current);
            }
            case 5:
            {
                return _iceD_removeSubscriber(this, in, current);
            }
        }

        assert(false);
        throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
    }
}