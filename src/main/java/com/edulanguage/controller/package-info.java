/**
 * <b>Tầng 1 - Presentation (Tầng trình bày / giao diện)</b>
 * <ul>
 *   <li>Controller: xử lý HTTP request/response, trả về view (web).</li>
 *   <li>Chỉ gọi các lớp thuộc tầng Service, không gọi Repository hay Entity trực tiếp (trừ khi chỉ đọc để hiển thị).</li>
 * </ul>
 */
package com.edulanguage.controller;
