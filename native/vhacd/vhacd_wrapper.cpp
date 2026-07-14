// c bridge between java and the v-hacd single header implementation
#define ENABLE_VHACD_IMPLEMENTATION 1
#include "VHACD.h"
#include "vhacd_wrapper.h"

#include <cstdlib>

extern "C" bvh_result* bvh_compute(const float* points, uint32_t pointCount,
								   const uint32_t* triangles, uint32_t triangleCount,
								   uint32_t maxHulls, uint32_t resolution, uint32_t maxVerticesPerHull)
{
	VHACD::IVHACD* solver = VHACD::CreateVHACD();
	VHACD::IVHACD::Parameters params;
	params.m_maxConvexHulls = maxHulls;
	params.m_resolution = resolution;
	params.m_maxNumVerticesPerCH = maxVerticesPerHull;
	// java owns the calling thread, keep everything on it
	params.m_asyncACD = false;

	bool ok = solver->Compute(points, pointCount, triangles, triangleCount, params);
	if (!ok || solver->GetNConvexHulls() == 0)
	{
		solver->Release();
		return nullptr;
	}

	uint32_t count = solver->GetNConvexHulls();
	bvh_result* result = (bvh_result*)std::malloc(sizeof(bvh_result));
	result->hullCount = count;
	result->hulls = (bvh_hull*)std::malloc(sizeof(bvh_hull) * count);

	for (uint32_t i = 0; i < count; ++i)
	{
		VHACD::IVHACD::ConvexHull hull;
		solver->GetConvexHull(i, hull);
		uint32_t n = (uint32_t)hull.m_points.size();
		result->hulls[i].pointCount = n;
		result->hulls[i].points = (float*)std::malloc(sizeof(float) * 3 * n);
		for (uint32_t p = 0; p < n; ++p)
		{
			result->hulls[i].points[p * 3] = (float)hull.m_points[p].mX;
			result->hulls[i].points[p * 3 + 1] = (float)hull.m_points[p].mY;
			result->hulls[i].points[p * 3 + 2] = (float)hull.m_points[p].mZ;
		}
	}

	solver->Release();
	return result;
}

extern "C" void bvh_free(bvh_result* result)
{
	if (result == nullptr)
	{
		return;
	}
	for (uint32_t i = 0; i < result->hullCount; ++i)
	{
		std::free(result->hulls[i].points);
	}
	std::free(result->hulls);
	std::free(result);
}
