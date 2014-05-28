


filename = '/Users/alexpardo/Desktop/oyster/Nov09JnyExport.csv'

output = 'tube_journeys.csv'

first = 1

day_maping = {'Mon':0, 'Tue':1, 'Wed':2, 'Thu':3, 'Fri':4, 'Sat':5, 'Sun':6}

parse_data = []
id = 1
with open(filename, 'r') as f:
	for line in f.readlines():
		if first: first = 0;continue;
		line = line.replace('"','')
		line = line.split(',') 
		if line[2] == 'LUL' and 'Unstarted' not in line[3] and 'Unfinished' not in line[4] and 'Not Applicable' not in line[3] and 'Not Applicable' not in line[4]:
			if (day_maping[line[1]]) != 1: continue;
			new = str(id) +','+ str(day_maping[line[1]]) +','+ line[3] +','+ line[4] +','+ line[5] +','+ line[7]
			id += 1
			parse_data.append(new)

with open(output, 'w') as f:
	f.write('\n'.join(parse_data))

