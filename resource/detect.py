import re

import cv2 as cv
import numpy as np
import pytesseract
from pytesseract import Output


def CropBlankBot(img):
    h, w = img.shape[0:2]
    last_row = -1
    for i in range(h - 1, 0, -1):
        for j in range(w - 1, 0, -1):
            if img[i][j] < 180:
                last_row = i
                break
        if last_row != -1:
            break
    return img[0:last_row + 10, 0:w]


#  纵向直线检测
def VerticalLineDetect(img):
    # Canny边缘检测
    edges = cv.Canny(img, 30, 240)

    # Hough直线检测
    minLineLength = 1200
    maxLineGap = 100
    lines = cv.HoughLinesP(edges, 1, np.pi / 180, 100, minLineLength, maxLineGap).tolist()
    sorted_lines = sorted(lines, key=lambda x: x[0])

    # 纵向直线列表
    vertical_lines = []
    for line in sorted_lines:
        for x1, y1, x2, y2 in line:
            # 在图片上绘制纵向直线
            if x1 == x2:
                vertical_lines.append((x1, y1, x2, y2))
                # vertical col num line debug
                # line_img = cv.line(img, (x1, y1), (x2, y2), (0, 0, 255), 2)
                # cv.imshow('line', line_img)
                # cv.waitKey(0)

    return vertical_lines


def execute(path):
    img = cv.imread(path)
    # img = cv.imread('nanjing.png') # change path

    # fore-extraction
    # gray and stretch
    img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    img = cv.resize(img, (0, 0), fx=3, fy=3, interpolation=cv.INTER_CUBIC)

    # extract col num
    lines_img = cv.inRange(img, 221, 221)
    thinned = cv.ximgproc.thinning(lines_img)
    col_num = int(len(VerticalLineDetect(thinned)) / 2 - 1)

    img_cropped = CropBlankBot(img)
    # debug crop, showing the bottom of cropped image
    # cv.imshow('cropped', img_cropped[int(img.shape[0]*0.9):img.shape[0],:])
    # cv.waitKey(0)

    # thresh for number extraction
    thresh_img = cv.threshold(img, 200, 255, cv.THRESH_BINARY + cv.THRESH_OTSU)[1]

    # tesseract start...
    d = pytesseract.image_to_data(thresh_img, output_type=Output.DICT)

    float_pattern = '^(-?\d+)(\.\d+)?$'  # number

    mmdd_pattern = '^((0([1-9]{1}))|(1[1|2]))-(([0-2]([1-9]{1}))|(3[0|1]))$'  # mm-dd

    time_pattern = '^(([0-1]\d)|(2[0-4])):[0-5]\d$'  # hh:mm

    val_list = []  # numbers
    mmdd_list = []
    time_list = []

    index_list = []  # index in data of tesseract

    n_boxes = len(d['text'])
    for i in range(n_boxes):
        if int(d['conf'][i]) > 10:
            if re.match(float_pattern, d['text'][i]):  # number
                val_list.append(d['text'][i])
                (x, y, w, h) = (d['left'][i], d['top'][i], d['width'][i], d['height'][i])
                print(x, y, w, h)
                thresh_img = cv.rectangle(thresh_img, (x, y), (x + w, y + h), (0, 255, 0), 1)
                index_list.append(i)

            if re.match(mmdd_pattern, d['text'][i]):
                mmdd_list.append(d['text'][i])
                (x, y, w, h) = (d['left'][i], d['top'][i], d['width'][i], d['height'][i])
                thresh_img = cv.rectangle(thresh_img, (x, y), (x + w, y + h), (0, 255, 0), 1)
            if re.match(time_pattern, d['text'][i]):
                time_list.append(d['text'][i])
                (x, y, w, h) = (d['left'][i], d['top'][i], d['width'][i], d['height'][i])
                thresh_img = cv.rectangle(thresh_img, (x, y), (x + w, y + h), (0, 255, 0), 1)

    # debug rectangles of parts
    # temp_img = cv.resize(thresh_img, (0, 0), fx=1/2, fy=1/2, interpolation=cv.INTER_CUBIC)
    # cv.imshow('rec', temp_img)
    # cv.waitKey(0)

    res_list = []  # 结果 list
    print(len(time_list), len(mmdd_list), len(val_list))

    # for i in range(len(time_list)):
    #     res_list.append([mmdd_list[i], time_list[i], val_list[i], 0, 0])
    #
    # row_num = len(mmdd_list)
    # rest_data_index = index_list[row_num:len(index_list)]
    # if len(rest_data_index) != 0:  # 如果有多的
    #     # treat table like a grid
    #     h, w = img_cropped.shape[0:2]
    #     col_w = w / col_num
    #     row_h = h / row_num
    #     for i in rest_data_index:
    #         row_index = int(d['top'][i] // row_h)
    #         col_index = int((d['left'][i] + d['width'][i] / 2) // col_w)
    #         print(row_index, col_index)
    #         if (col_index != 2 and col_index != 3):
    #             print('wrong')
    #         else:
    #             res_list[row_index][col_index] = d['text'][i]
    # return res_list


execute("D:/zhuomian/test/png/nanjing.png")