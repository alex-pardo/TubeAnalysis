import networkx as nx
import matplotlib.pyplot as plt
import numpy as np
from itertools import imap
# import Image
# import pandas as pd
# from mpl_toolkits.basemap import Basemap
# from matplotlib.offsetbox import OffsetImage, AnnotationBbox
# from shapely.geometry import Point, Polygon, MultiPoint, MultiPolygon
# from descartes import PolygonPatch
# from matplotlib.collections import PatchCollection
# import brewer2mpl

colors = [] #['#0e207f', '#6d21a3', '#e3dc7f', '#a4dba3', '#e8568e', '#82b8d0', '#00a8b8', '#322e2b', '#605076', '#eeb402', '#0f9709', '#970909', '#40ffea']
lines = {}
def createNetwork():
	G = nx.MultiGraph()

	pos = {}
	with open("lines.csv", 'r') as f:
		first = True
		for line in f.readlines():
			if first:
				first = False
				continue
			else:
				data = line.split(',')
				lines[data[0]] = data[1]
				colors.append('#'+data[2].replace('\"',''))

	m_s = []
	with open("stations.csv", 'r') as f:
		first = True
		for line in f.readlines():
			if first:
				first = False
				continue
			else:
				data = line.split(',')
				# "id", "latitude","longitude","name","display_name","zone","total_lines","rail"
				id = int(data[0])
				l = float(data[1])
				ll = float(data[2])
				name = str(data[3]).replace('\"', '')
				size = int(data[6])
				if size not in m_s:
					m_s.append(size)
				G.add_node(id, lat=l, long=ll, name=name, size=size)
				pos[id] = [l, ll]
	with open("connections.csv", 'r') as f:
		first = True
		for line in f.readlines():
			if first:
				first = False
				continue
			else:
				data = line.split(',')
				s1 = int(data[0])
				s2 = int(data[1])
				line = int(data[2])
				w = np.round(haversine_km(G.node[s1]['lat'], G.node[s1]['long'], G.node[s2]['lat'], G.node[s2]['long'])*1000)
				# print 'Distance between ', G.node[s1]['name'], 'and', G.node[s2]['name'], ':',w
				G.add_edge(s1,s2,line=line, weight= int(w))
	print m_s
	#return
	nx.write_graphml(G, 'tube_map.graphml')
	print 'Generating paths'
	saveMinPaths(G)
	#plotPositions(G, pos)


def haversine_km(lat1, long1, lat2, long2):
	d2r = np.pi/180.0
	dlong = (long2 - long1) * d2r
	dlat = (lat2 - lat1) * d2r
	a = np.sin(dlat/2.0)**2 + np.cos(lat1*d2r) * np.cos(lat2*d2r) * np.sin(dlong/2.0)**2
	c = 2 * np.arctan2(np.sqrt(a), np.sqrt(1-a))
	d = 6367 * c
	return d


def saveMinPaths(G):
	p=nx.shortest_path(G, weight='weight')
	with open('paths.csv','w') as f:
		for source in p:
			for destinatiton in p[source]:
				if source == destinatiton:
					continue
				f.write(str(source)+','+str(destinatiton)+','+ ",".join(imap(str, p[source][destinatiton]))+'\n')


def plotPositions(G, pos):
	# Set up the basemap and plot the markers.
	lats = []
	lons = []
	ldn_points = []
	for p in pos:
		lats.append(pos[p][0])
		lons.append(pos[p][1])
		ldn_points.append([pos[p][0], pos[p][1]])

	# print lats
	# lats = np.arange(lats)
	# lons = np.arange(lons)
	
	m = Basemap(projection='merc',
	            llcrnrlon=min(lons) - 2, llcrnrlat=min(lats) - 2,
	            urcrnrlon=max(lons) + 2, urcrnrlat=max(lats) + 2,
	            resolution='i')

	m.readshapefile('./london/london_wards','london', color='none', zorder=2)
	df_map = pd.DataFrame({'poly': [Polygon(xy) for xy in m.london],'ward_name': [ward['NAME'] for ward in m.london_info]})
	df_map['area_m'] = df_map['poly'].map(lambda x: x.area)
	df_map['area_km'] = df_map['area_m'] / 100000

	# # Create Point objects in map coordinates from dataframe lon and lat values
	# map_points = pd.Series(
	#     [Point(m(mapped_x, mapped_y)) for mapped_x, mapped_y in zip(df['lon'], df['lat'])])
	# plaque_points = MultiPoint(list(map_points.values))
	# wards_polygon = prep(MultiPolygon(list(df_map['poly'].values)))
	# # calculate points that fall within the London boundary
	# ldn_points = filter(wards_polygon.contains, plaque_points)

	df_map['patches'] = df_map['poly'].map(lambda x: PolygonPatch(x, fc='#ffffff', ec='#000000', lw=.25, alpha=.9, zorder=4))



	plt.clf()
	fig = plt.figure(1)

	# x,y = m(lons,lats)


	ax = fig.add_subplot(111, axisbg='w', frame_on=True)

	
	ax.add_collection(PatchCollection(df_map['patches'].values, match_original=True))
	
	plt.title("London tube")
	
	plotted_lines = []

	for edge in G.edges(data=True):
		s1 = G.node[edge[0]]
		s2 = G.node[edge[1]]
		color = colors[edge[2]['line']-1]
		x1,y1 = m(s1['long'],s1['lat'])
		x2,y2 = m(s2['long'],s2['lat'])
		plt.hold('on')

		label = ''
		if edge[2]['line'] not in plotted_lines:
			label = lines[str(edge[2]['line'])]
			plotted_lines.append(edge[2]['line'])

		m.plot(x1, y1, '.', markersize=2+s1['size']*2, color=color, alpha=0.8, label='')
		plt.hold('on')
		m.plot([x1, x2], [y1, y2], '-', color = color, linewidth=3, alpha=0.8, label=label)
		plt.hold('on')
		m.plot(x2, y2, '.', markersize=2+s2['size']*2, color=color, alpha=0.8, label='')
		plt.hold('on')

	plt.legend(loc=3,prop={'size':10})
		

	plt.tight_layout()
	# this will set the image width to 722px at 100dpi
	fig.set_size_inches(7.22, 5.25)

	plt.xlim(209821, 325532)
	plt.ylim(325618, 406819)

	plt.show()

createNetwork()

