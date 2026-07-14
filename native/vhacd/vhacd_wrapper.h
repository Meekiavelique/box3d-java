// plain c face over v-hacd so jextract can bind it
#pragma once

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// one convex piece of the decomposition, points are xyz triples
typedef struct bvh_hull
{
	float* points;
	uint32_t pointCount;
} bvh_hull;

typedef struct bvh_result
{
	bvh_hull* hulls;
	uint32_t hullCount;
} bvh_result;

// runs the full decomposition synchronously, returns null on failure
bvh_result* bvh_compute(const float* points, uint32_t pointCount,
						const uint32_t* triangles, uint32_t triangleCount,
						uint32_t maxHulls, uint32_t resolution, uint32_t maxVerticesPerHull);

void bvh_free(bvh_result* result);

#ifdef __cplusplus
}
#endif
