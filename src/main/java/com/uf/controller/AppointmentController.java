package com.uf.controller;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.uf.domain.Appointment;
import com.uf.domain.Users;
import com.uf.services.AppointmentService;
import com.uf.services.UserService;

@Controller
@RequestMapping("/appointment")
public class AppointmentController {

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String createAppointment(Model model) {
		Appointment appointment = new Appointment();
		model.addAttribute("appointment", appointment);
		model.addAttribute("dateString", "");
		return "appointment";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String createAppointmentPost(@ModelAttribute("appointment") Appointment appointment, @ModelAttribute("dateString") String date, Model model, Principal principal) throws ParseException {
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		Date d1 = format1.parse(date);
		appointment.setDateAppointement(d1);
		Users user = userService.findByUsername(principal.getName());
		appointment.setUser(user);
		appointmentService.createAppointment(appointment);
		return "redirect:/userFront";
	}
	
	@RequestMapping("/appointments")
	public String getAllApointments(Model model, Principal principal) {
		List<Appointment> listAppointments = appointmentService.findAll();
		model.addAttribute("allApointments", listAppointments);
		return "appointments";
	}
}
