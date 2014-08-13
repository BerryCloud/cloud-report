package com.berrycloud.cloudreport;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.ServiceAbbreviations;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

@Controller
public class EC2InstancesController {

	static final Logger LOG = LoggerFactory
			.getLogger(EC2InstancesController.class);

	/**
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {

		LOG.debug("EC2");

		Set<Instance> instances = new HashSet<Instance>();

		for (Region region : RegionUtils
				.getRegionsForService(ServiceAbbreviations.EC2)) {

			LOG.debug("Region Name: " + region.getName());

			AmazonEC2 ec2 = region.createClient(AmazonEC2Client.class, null,
					null);
			ec2.setRegion(region);

			try {

				DescribeInstancesResult instancesResult = ec2
						.describeInstances();


				for (Reservation reservation : instancesResult.getReservations()) {
					instances.addAll(reservation.getInstances());
				}

			}
			catch (AmazonClientException e) {
				LOG.debug("Getting EC2 Instances failed");

			}

			model.addAttribute("instances", instances);

		}
		
		return "ec2";
	}
}