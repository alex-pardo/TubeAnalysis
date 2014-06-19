import networkx as nx
from Tkinter import Tk
from tkFileDialog import askopenfilename

Tk().withdraw() # we don't want a full GUI, so keep the root window from appearing

graphml = askopenfilename()

G = nx.read_graphml(graphml)
G = nx.convert_node_labels_to_integers(G)
print len(G.nodes())
print len(G.edges())
nx.write_pajek(G,graphml.replace("graphml", "net"))
