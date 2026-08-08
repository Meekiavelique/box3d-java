// Rollback support. Not part of upstream Box3D.
//
// SPDX-License-Identifier: MIT

#pragma once

#include "box3d/types.h"

#ifdef __cplusplus
extern "C"
{
#endif

/// Restore warm starting impulses onto an existing contact. Points are matched by feature id, so ones
/// that no longer exist are skipped and ones with nothing saved keep what the solver just produced.
/// @return the number of manifold points that were restored
B3_API int b3Contact_RestoreImpulses( b3ContactId contactId, const b3Manifold* manifolds, int manifoldCount );

/// Get the sleep time a body has accumulated, so a rollback can put it back along with the pose.
B3_API float b3Body_GetSleepTime( b3BodyId bodyId );

/// Set a previously read sleep time. Leaves the awake flag alone, pair it with b3Body_SetAwake.
B3_API void b3Body_SetSleepTime( b3BodyId bodyId, float sleepTime );

#ifdef __cplusplus
}
#endif
