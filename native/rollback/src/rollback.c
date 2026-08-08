// Rollback support. Not part of upstream Box3D.
// SPDX-License-Identifier: MIT
#include "box3d/box3d_rollback.h"

#include "body.h"
#include "contact.h"
#include "physics_world.h"

#include "box3d/box3d.h"

static b3Contact* b3RollbackGetContact( b3World* world, b3ContactId contactId )
{
	int id = contactId.index1 - 1;
	b3Contact* contact = b3Array_Get( world->contacts, id );
	B3_ASSERT( contact->contactId == id && contact->generation == contactId.generation );
	return contact;
}

static int b3RestoreManifoldImpulses( b3Manifold* target, const b3Manifold* source )
{
	target->twistImpulse = source->twistImpulse;
	target->frictionImpulse = source->frictionImpulse;
	target->rollingImpulse = source->rollingImpulse;

	int restored = 0;
	for ( int targetPoint = 0; targetPoint < target->pointCount; ++targetPoint )
	{
		for ( int sourcePoint = 0; sourcePoint < source->pointCount; ++sourcePoint )
		{
			if ( target->points[targetPoint].featureId != source->points[sourcePoint].featureId )
			{
				continue;
			}

			target->points[targetPoint].normalImpulse = source->points[sourcePoint].normalImpulse;
			target->points[targetPoint].totalNormalImpulse = source->points[sourcePoint].totalNormalImpulse;
			target->points[targetPoint].persisted = true;
			restored += 1;
			break;
		}
	}

	return restored;
}

int b3Contact_RestoreImpulses( b3ContactId contactId, const b3Manifold* manifolds, int manifoldCount )
{
	if ( manifolds == NULL || manifoldCount <= 0 )
	{
		return 0;
	}

	b3World* world = b3GetWorld( contactId.world0 );
	b3Contact* contact = b3RollbackGetContact( world, contactId );

	int count = manifoldCount < contact->manifoldCount ? manifoldCount : contact->manifoldCount;
	int restored = 0;
	for ( int index = 0; index < count; ++index )
	{
		restored += b3RestoreManifoldImpulses( contact->manifolds + index, manifolds + index );
	}

	return restored;
}

float b3Body_GetSleepTime( b3BodyId bodyId )
{
	b3World* world = b3GetWorld( bodyId.world0 );
	b3Body* body = b3GetBodyFullId( world, bodyId );
	return body->sleepTime;
}

void b3Body_SetSleepTime( b3BodyId bodyId, float sleepTime )
{
	b3World* world = b3GetWorld( bodyId.world0 );
	b3Body* body = b3GetBodyFullId( world, bodyId );
	body->sleepTime = sleepTime;
}
