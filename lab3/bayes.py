import numpy as np
from matplotlib import pyplot as plt

greenData = np.genfromtxt('data1.csv', delimiter='|')
redData = np.genfromtxt('data2.csv', delimiter='|')
RADIUS = 1

def showData(greenData, redData, newPoint):
    circle = plt.Circle(newPoint, RADIUS, color='k', fill=False)

    fig, ax = plt.subplots() # note we must use plt.subplots, not plt.subplot
    # (or if you have an existing figure)
    # fig = plt.gcf()
    # ax = fig.gca()

    ax.add_artist(circle)
    plt.scatter(greenData[:, 0], greenData[:, 1],  color='g')
    plt.scatter(redData[:, 0], redData[:, 1],  color='r')
    plt.scatter(newPoint[0], newPoint[1],  color='k')
    plt.gca().set_aspect('equal', adjustable='box')
    plt.show()


def clasify(greenData_, redData_, newPoint):
    aprioriGreen = greenData_.shape[0] / (greenData_.shape[0] + redData_.shape[0])
    aprioriRed = redData_.shape[0] / (greenData_.shape[0] + redData_.shape[0])

    chance1 = 0
    chance2 = 0

    for point in greenData_:
        if np.linalg.norm(newPoint - point) < RADIUS:
            chance1 += 1
    chance1 /= greenData.shape[0]

    for point in redData_:
        if np.linalg.norm(newPoint - point) < RADIUS:
            chance2 += 1

    chance2 /= redData.shape[0]
    aposteroriGreen = chance1 * aprioriGreen
    aposteroriRed = chance2 * aprioriRed
    decision = None
    if(aposteroriGreen > aposteroriRed):
        decision = 0
    else:
        decision = 1

    return aposteroriGreen, aposteroriRed, decision

newPoint = np.random.random( 2) * np.array([2, 4]) + np.array([0, 2])
print(newPoint)
probGreen, probRed, decision = clasify(greenData, redData, newPoint)
print('Probability of being red: {} and green: {}'.format(probRed, probGreen))
if decision == 0:
    print('Classified as green')
else:
    print('Classified as red')
showData(greenData, redData, newPoint)