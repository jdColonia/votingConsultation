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

public interface VotingService extends com.zeroc.Ice.Object
{
    ConsultationResponse getVotingStation(String voterId, com.zeroc.Ice.Current current)
        throws SystemException,
               VoterNotFoundException;

    ConsultationResponse[] getMultipleVotingStations(String[] voterIds, com.zeroc.Ice.Current current)
        throws SystemException;

    /** @hidden */
    static final String[] _iceIds =
    {
        "::Ice::Object",
        "::VotingConsultation::VotingService"
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
        return "::VotingConsultation::VotingService";
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
     * @throws com.zeroc.Ice.UserException -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_getVotingStation(VotingService obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
        throws com.zeroc.Ice.UserException
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String iceP_voterId;
        iceP_voterId = istr.readString();
        inS.endReadParams();
        ConsultationResponse ret = obj.getVotingStation(iceP_voterId, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ConsultationResponse.ice_write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /**
     * @hidden
     * @param obj -
     * @param inS -
     * @param current -
     * @return -
     * @throws com.zeroc.Ice.UserException -
    **/
    static java.util.concurrent.CompletionStage<com.zeroc.Ice.OutputStream> _iceD_getMultipleVotingStations(VotingService obj, final com.zeroc.IceInternal.Incoming inS, com.zeroc.Ice.Current current)
        throws com.zeroc.Ice.UserException
    {
        com.zeroc.Ice.Object._iceCheckMode(null, current.mode);
        com.zeroc.Ice.InputStream istr = inS.startReadParams();
        String[] iceP_voterIds;
        iceP_voterIds = istr.readStringSeq();
        inS.endReadParams();
        ConsultationResponse[] ret = obj.getMultipleVotingStations(iceP_voterIds, current);
        com.zeroc.Ice.OutputStream ostr = inS.startWriteParams();
        ConsultationResponseSeqHelper.write(ostr, ret);
        inS.endWriteParams(ostr);
        return inS.setResult(ostr);
    }

    /** @hidden */
    final static String[] _iceOps =
    {
        "getMultipleVotingStations",
        "getVotingStation",
        "ice_id",
        "ice_ids",
        "ice_isA",
        "ice_ping"
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
                return _iceD_getMultipleVotingStations(this, in, current);
            }
            case 1:
            {
                return _iceD_getVotingStation(this, in, current);
            }
            case 2:
            {
                return com.zeroc.Ice.Object._iceD_ice_id(this, in, current);
            }
            case 3:
            {
                return com.zeroc.Ice.Object._iceD_ice_ids(this, in, current);
            }
            case 4:
            {
                return com.zeroc.Ice.Object._iceD_ice_isA(this, in, current);
            }
            case 5:
            {
                return com.zeroc.Ice.Object._iceD_ice_ping(this, in, current);
            }
        }

        assert(false);
        throw new com.zeroc.Ice.OperationNotExistException(current.id, current.facet, current.operation);
    }
}
