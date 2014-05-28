import networkx as nx
import matplotlib.pyplot as plt
import numpy as np


G = nx.read_graphml('tube_map.graphml')
# max_vals = [float('-inf'), float('-inf')]
# min_vals = [float('inf'), float('inf')]
positions = {}
for node in G.nodes(data=True):
	positions[node[0]] = [node[1]['long'], node[1]['lat']]
# 	if max_vals[0] < node[1]['lat']: max_vals[0] = node[1]['lat'];
# 	if max_vals[1] < node[1]['long']: max_vals[1] = node[1]['long'];
# 	if min_vals[0] > node[1]['lat']: min_vals[0] = node[1]['lat'];
# 	if min_vals[0] > node[1]['long']: min_vals[1] = node[1]['long'];

# print max_vals
# print min_vals


#f1 = plt.figure(1)
#nodes=nx.draw_networkx(G,pos=positions, node_size=10, with_labels=False)
#plt.show()

# import stations
stations = {}
first = 1
with open('stations.csv', 'r') as f:
	for line in f.readlines():
		if first: first = 0;continue;
		line = line.replace('"','')
		line = line.split(',')
		stations[line[3]] = line[0]


data = []
print 'Total number of journeys:', sum(1 for line in open('tube_journeys.csv'))
with open('tube_journeys.csv', 'r') as f:
	for line in f.readlines():
		line = line.replace('\n','')
		line = line.split(',')
		line[0] = int(line[0])
		line[1] = int(line[1])
		line[2] = int(stations[line[2]])
		line[3] = int(stations[line[3]])
		line[4] = int(line[4])
		line[5] = int(line[5])
		data.append(line)
			
		
#plt.hold('on')
#plt.show(block=False)

data = np.matrix(data)

start = data[np.argsort(data.A[:, 4])]
end = data[np.argsort(data.A[:, 5])]

current_time = start[0,4]
init_time = current_time
plots = {}
f2 = plt.figure(2)
#plt.show(block=False)
#plt.hold('on')
travelers = []
curr_travelers = 0
while len(end) > 0:
	# plt.figure(f1.number)
	# plt.title(current_time)

	while len(start) > 0  and start[0,4] <= current_time:
		#s = start[0,:].tolist()[0]

		#res = plt.plot(positions[str(s[2])][0],positions[str(s[2])][1], 'o', color='blue')
		#plots[str(s[0])] = res
		#plt.draw()
		#plt.pause(0.0001)
		start = np.delete(start, 0, 0)
		curr_travelers += 1

	

	

	while len(end) > 0 and end[0,4] <= current_time:
		# e = end[0,:].tolist()[0]
		
		# try:
		# 	tmp = plots[str(e[0])][0]
		# 	tmp.remove()
		# except Exception, e2:
		# 	print plots
		# 	print e
		# 	raise e2
		# del plots[str(e[0])]
		end = np.delete(end, 0, 0)
		curr_travelers -= 1



	travelers.append(curr_travelers)
	# plt.draw()
	# plt.pause(0.0001)

	# plt.figure(f2.number)
	# if len(travelers) > 2:
	# 	tmp = np.linspace(init_time, current_time, len(travelers))
	# 	plt.plot(tmp, travelers, '-b')
	# 	plt.title(current_time)
	# 	plt.draw()
	#time.sleep(0.05)
	current_time += 1

plt.figure(f2.number)
tmp = np.linspace(init_time, current_time, len(travelers))
plt.plot(tmp, travelers, '-b')
plt.title(current_time)
plt.show()






